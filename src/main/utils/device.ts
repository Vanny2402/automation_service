import { EventEmitter } from 'events'
import { Command } from './helper'
import { adbBinaryPath } from './utils'

export class DeviceMonitor extends EventEmitter {
  private pollingInterval: NodeJS.Timeout | null = null

  constructor() {
    super()
    this.startMonitoring()
  }

  startMonitoring(interval = 5000) {
    this.pollingInterval = setInterval(() => {
      this.checkForDevices()
    }, interval)
  }

  stopMonitoring() {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval)
      this.pollingInterval = null
    }
  }

  checkForDevices() {
    checkForDevice((devices) => {
      if (devices.length > 0) {
        this.emit('devicesDetected', devices)
      }
    })
  }
}

function checkForDevice(callback: (devices: string[]) => void) {
  Command(adbBinaryPath, 'devices', (output) => {
    const devices = output
      .split('\n')
      .slice(1)
      .filter((line) => line.trim() !== '' && !line.includes('offline'))
      .map((line) => line.split('\t')[0])

    if (devices.length === 0) {
      console.error('No devices connected.')
    } else {
      callback(devices)
    }
  })
}

function uninstallServer(device: string, callback: () => void): void {
  Command(
    adbBinaryPath,
    `-s ${device} uninstall io.appium.uiautomator2.server`,
    (output: string) => {
      console.log(`Uninstall io.appium.uiautomator2.server on ${device}:`, output)
      callback()
    }
  )
}

function uninstallServerTest(device: string, callback: () => void): void {
  Command(
    adbBinaryPath,
    `-s ${device} uninstall io.appium.uiautomator2.server.test`,
    (output: string) => {
      console.log(`Uninstall io.appium.uiautomator2.server.test on ${device}:`, output)
      callback()
    }
  )
}

export function processDeleteOldServerTest(devices: string[], callback: () => void): void {
  let index = 0

  function next(): void {
    if (index < devices.length) {
      const device = devices[index++]
      uninstallServer(device, () => {
        uninstallServerTest(device, callback)
      })
    } else {
      callback()
    }
  }

  next()
}
