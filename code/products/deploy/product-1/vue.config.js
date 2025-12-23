module.exports = {
  // ...other vue-cli plugin options...
  pwa: {
    // configure the workbox plugin
    workboxPluginMode: "InjectManifest",
    workboxOptions: {
      // swSrc is required in InjectManifest mode.
      // we specify a custom service-worker in this route
      swSrc: "src/service-worker.js",
      // ...other Workbox options...
    },
  },
  publicPath: "/product-1/demo",
  // Used to import .sld files when creating a custom SLD style
  chainWebpack: (config) => {
    config.module
      .rule("raw")
      .test(/\.sld$/)
      .use("raw-loader")
      .loader("raw-loader")
      .end();
  },
  parallel: false,
};
