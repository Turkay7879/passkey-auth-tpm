import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import packageJson from './package.json';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/api": {
        target: packageJson.backendServer,
        changeOrigin: true,
      }
    }
  }
})
