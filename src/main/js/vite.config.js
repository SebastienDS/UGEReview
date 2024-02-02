import { svelte } from '@sveltejs/vite-plugin-svelte';
import { defineConfig } from 'vite';

export default defineConfig({
  plugins: [
    svelte()
  ],
  build: {
    rollupOptions: {
      input: {
        app: '/src/main/js/index.html'
      },
      output: {
        dir: 'target/classes/static/build',
        entryFileNames: 'bundle.js'
      }
    }
  }
})
