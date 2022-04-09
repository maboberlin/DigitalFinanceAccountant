// vue.config.js
module.exports = {
    devServer: {
        port: 3000,
        proxy: {
            '/api': {
                target: 'http://localhost:30303',
                ws: true,
                changeOrigin: true
            }
        }
    }
}