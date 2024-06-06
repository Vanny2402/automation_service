import path from 'path'
import { platform } from 'os'
import { app } from 'electron'

export function getPlatform() {
  switch (platform()) {
    case 'aix':
    case 'freebsd':
    case 'linux':
    case 'openbsd':
    case 'android':
      return 'linux'
    case 'darwin':
    case 'sunos':
      return 'mac'
    case 'win32':
      return 'win'
    default:
      throw new Error('Unsupported platform')
  }
}

export function getBinariesPath() {
  const { isPackaged } = app

  const binariesPath = isPackaged
    ? path.join(process.resourcesPath, 'bin')
    : path.join(app.getAppPath(), 'resources', getPlatform())

  return binariesPath
}

export const adbDir = getBinariesPath()

export const adbBinaryPath = path.resolve(
  path.join(getBinariesPath(), process.platform === 'win32' ? 'adb.exe' : 'adb')
)
