import { join } from 'path'
import { electronApp, optimizer } from '@electron-toolkit/utils'
import { app, BrowserWindow, ipcMain, Tray, nativeImage, Menu } from 'electron'
// import icon from '../../resources/icon.png?asset'
import { exec } from 'child_process'
import NodeServer from './components/node-server'
import { adbBinaryPath, adbDir } from './utils/utils'
import { DeviceMonitor, processDeleteOldServerTest } from './utils/device'
import Store from 'electron-store'

let tray: Tray | null = null
const servers: { [port: string]: NodeServer } = {}
const appiumPort: number = 4723
const deviceMonitor = new DeviceMonitor()

type StoreType = {
  connectedDevices: string[]
}

const store = new Store<StoreType>({
  defaults: {
    connectedDevices: []
  }
})

// function createWindow(): void {
//   const mainWindow = new BrowserWindow({
//     width: 900,
//     height: 670,
//     show: false,
//     autoHideMenuBar: true,
//     ...(process.platform === 'linux' ? { icon } : {}),
//     webPreferences: {
//       preload: join(__dirname, '../preload/index.js'),
//       sandbox: false
//     }
//   })

//   mainWindow.on('ready-to-show', () => {
//     mainWindow.show()
//   })

//   mainWindow.webContents.setWindowOpenHandler((details) => {
//     shell.openExternal(details.url)
//     return { action: 'deny' }
//   })

//   if (is.dev && process.env['ELECTRON_RENDERER_URL']) {
//     mainWindow.loadURL(process.env['ELECTRON_RENDERER_URL'])
//   } else {
//     mainWindow.loadFile(join(__dirname, '../renderer/index.html'))
//   }
// }

function createTray(): void {
  try {
    const iconPath = join(app.getAppPath(), 'resources', 'icon.png')
    const trayNatvieImg = nativeImage.createFromPath(iconPath)
    const trayIcon = trayNatvieImg.resize({ width: 19, height: 19 })
    tray = new Tray(trayIcon)

    const contextMenu = Menu.buildFromTemplate([
      { label: 'Menu Header', type: 'normal', enabled: false },
      { type: 'separator' },
      {
        label: 'Restart',
        click: () => restart()
      },
      {
        label: 'Quit',
        click: () => quit()
      }
    ])

    tray.setContextMenu(contextMenu)
  } catch (error) {
    console.error('Error creating tray:', error)
  }
}

app.whenReady().then(() => {
  process.env.ANDROID_HOME = adbDir
  electronApp.setAppUserModelId('com.electron')
  app.on('browser-window-created', (_, window) => {
    optimizer.watchWindowShortcuts(window)
  })

  ipcMain.on('ping', () => console.log('pong'))
  deviceMonitor.on('devicesDetected', (devices) => {
    let connectedDevices = store.get('connectedDevices') || []
    const newDevices = devices.filter((device) => !connectedDevices.includes(device))

    if (newDevices.length > 0) {
      processDeleteOldServerTest(newDevices, () => {
        console.log('Old server test deleted successfully.')
      })
      store.set(
        'connectedDevices',
        (connectedDevices = connectedDevices.concat(
          newDevices.filter((device) => !connectedDevices.includes(device))
        ))
      )
    } else {
      console.log('No new devices detected.')
    }
  })

  createTray()
  // createWindow()
  try {
    startADB()
  } catch (adbError) {
    console.error('Error starting ADB:', adbError)
  }

  try {
    startAppium(appiumPort)
  } catch (appiumError) {
    console.error('Error starting Appium:', appiumError)
  }

  app.on('activate', function () {
    process.env.ANDROID_HOME = adbDir
    if (BrowserWindow.getAllWindows().length === 0) {
      createTray()
      try {
        startADB()
      } catch (adbError) {
        console.error('Error starting ADB:', adbError)
      }

      try {
        startAppium(appiumPort)
      } catch (appiumError) {
        console.error('Error starting Appium:', appiumError)
      }
      // createWindow()
    }
  })
})

app.on('window-all-closed', async () => {
  try {
    if (process.platform !== 'darwin') {
      try {
        await stopADB()
      } catch (adbError) {
        console.error('Error stopping ADB:', adbError)
      }

      try {
        await stopAppium(appiumPort)
      } catch (appiumError) {
        console.error('Error stopping Appium:', appiumError)
      }

      app.quit()
    }
  } catch (error) {
    console.error('Error while handling window all closed event:', error)
  }
})

function startADB(): Promise<void> {
  return new Promise<void>((resolve, reject) => {
    exec(`"${adbBinaryPath}" start-server`, (error, stdout, stderr) => {
      if (error) {
        console.error(`Error starting adb server: ${error.message}`)
        reject(error)
        return
      }
      if (stderr) {
        console.error(`ADB stderr: ${stderr}`)
      }
      console.log(`ADB stdout: ${stdout}`)
      resolve()
    })
  })
}

function stopADB(): Promise<void> {
  return new Promise<void>((resolve, reject) => {
    exec(`"${adbBinaryPath}" kill-server`, (error, stdout, stderr) => {
      if (error) {
        console.error(`Error stopping adb server: ${error.message}`)
        reject(error)
        return
      }
      if (stderr) {
        console.error(`ADB stderr: ${stderr}`)
      }
      console.log(`ADB stdout: ${stdout}`)
      resolve()
    })
  })
}

function isPortInUse(port: number): Promise<boolean> {
  return new Promise((resolve, reject) => {
    const command = process.platform === 'win32' ? 'netstat -a -n -o' : 'lsof -i -n -P'

    const child = exec(command, (error, stdout) => {
      if (error) {
        reject(error)
        return
      }

      const outputLines = stdout.split('\n')
      const portLine = outputLines.find((line) => line.includes(`:${port}`))

      if (portLine) {
        resolve(true)
      } else {
        resolve(false)
      }
    })

    child.on('exit', (code) => {
      if (code !== 0) {
        reject(new Error(`Child process exited with code ${code}`))
      }
    })
  })
}

async function startAppium(port: number): Promise<NodeServer | null> {
  try {
    const portInUse = await isPortInUse(port)

    if (portInUse) {
      await stopAppium(port)
    }

    const appiumServer = new NodeServer({ port: port })
    await appiumServer.start()
    servers[port] = appiumServer
    console.log(`Appium server started on port ${port}`)
    return appiumServer
  } catch (error) {
    console.error('Error starting Appium:', error)
    throw error
  }
}

async function stopAppium(port: number): Promise<void> {
  try {
    const appiumServer = servers[port]
    if (appiumServer) {
      await appiumServer.stop()
      delete servers[port]
      console.log(`Appium server on port ${port} stopped.`)
    } else {
      console.log(`No Appium server running on port ${port}.`)
    }
  } catch (error) {
    console.error('Error stopping Appium:', error)
    throw error
  }
}

async function quit(): Promise<void> {
  try {
    try {
      await stopADB()
    } catch (adbError) {
      console.error('Error stopping ADB:', adbError)
    }

    try {
      await stopAppium(appiumPort)
    } catch (appiumError) {
      console.error('Error stopping Appium:', appiumError)
    }
    store.clear()
    app.quit()
    console.log('Application quit successfully.')
  } catch (error) {
    console.error('Error while quitting application:', error)
  }
}

async function restart(): Promise<void> {
  try {
    try {
      await stopADB()
    } catch (adbError) {
      console.error('Error stopping ADB:', adbError)
    }

    try {
      await stopAppium(appiumPort)
    } catch (appiumError) {
      console.error('Error stopping Appium:', appiumError)
    }
    app.exit(0)
    app.relaunch()

    console.log('Application restarted successfully.')
  } catch (error) {
    console.error('Error during restart process:', error)
  }
}
