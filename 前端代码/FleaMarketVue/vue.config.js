const webpack = require('webpack')

module.exports = {

    publicPath: './',
    assetsDir: 'static',
    productionSourceMap: false,
    configureWebpack: {
        plugins: [
            new webpack.ProvidePlugin({
                $: "jquery",
                jQuery: "jquery",
                "windows.jQuery": "jquery"
            })
        ]
    },
    devServer: {
        host: 'localhost',
        port: 9322,
        proxy: {
            '/api':{
                target:'http://localhost:9321',
                changeOrigin:true,
                pathRewrite:{
                    '/api':'http://localhost:9321'
                }
            }
        }
    }
};