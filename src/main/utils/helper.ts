import { exec } from 'child_process'

export function Command(
  BinaryPath: string | null = null,
  command: string,
  callback: (output: string) => void
) {
  exec(`${BinaryPath || ''} ${command}`, (error, stdout, stderr) => {
    if (error) {
      console.error(`ADB command error: ${error.message}`)
      return
    }
    if (stderr) {
      console.error(`command error: ${stderr}`)
    }
    callback(stdout.trim())
  })
}
