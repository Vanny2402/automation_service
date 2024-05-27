import { main as startAppiumServer } from 'appium/build/lib/main'

interface CustomServer {
  close(): Promise<void>
}

class NodeServer {
  port: number
  server: CustomServer | null

  constructor(options: { port: number }) {
    this.port = options.port
    this.server = null
  }

  async start(): Promise<CustomServer> {
    try {
      const serverInstance = await startAppiumServer({ port: this.port })
      const customServerInstance = serverInstance as unknown as CustomServer
      this.server = customServerInstance
      console.log(`Server started on port ${this.port}`)
      return customServerInstance
    } catch (error) {
      console.error(`Error starting server on port ${this.port}:`, error)
      throw error
    }
  }

  async stop(): Promise<void> {
    try {
      if (this.server) {
        await this.server.close()
        console.log(`Server on port ${this.port} stopped`)
        this.server = null
      } else {
        console.log(`No server running on port ${this.port}`)
      }
    } catch (error) {
      console.error(`Error stopping server on port ${this.port}:`, error)
      throw error
    }
  }
}

export default NodeServer
