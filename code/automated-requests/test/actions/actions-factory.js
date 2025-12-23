const fs = require("fs");
const path = require("path");

const actions = {};

const actionDirectory = path.join(__dirname, "."); // Ajusta la ruta según donde estén tus archivos

function wrapFunction(fn, name) {
  return function (...args) {
      const result = fn.apply(this, args);
      return result;
  };
}

function wrapObjectMethods(obj, repoKey) {
  return new Proxy(obj, {
      get(target, prop, receiver) {
          if (typeof target[prop] === "function") {
              return wrapFunction(target[prop], `${repoKey}.${prop}`);
          }
          return Reflect.get(target, prop, receiver);
      }
  });
}

// Lee todos los archivos en el directorio especificado
fs.readdirSync(actionDirectory).forEach((file) => {
  // Filtra los archivos que terminan en -action.js
  if (file.endsWith("-action.js")) {
    const repoKey = file.replace("-action.js", "").toLowerCase();
    const repo = require(path.join(actionDirectory, file));
    actions[repoKey] = wrapObjectMethods(repo, repoKey);;
  }
});

module.exports = {
  get: (name) => actions[name.toLowerCase()],
};
