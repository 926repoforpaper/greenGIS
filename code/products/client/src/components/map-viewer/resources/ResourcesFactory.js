const resources = {};

const resourcesFiles = require.context("./types", true, /\.js$/);

// Dynamic import of Resources
// NOTE: For this algorithm work properly,
// all files in 'types' folder must have different names
resourcesFiles.keys().forEach((filename) => {
  Object.keys(resourcesFiles(filename)).forEach((key) => {
    //Format key from './Filename.js' to 'Filename'
    let nameParts = filename.split("/");
    let relativeName = nameParts[nameParts.length - 1];
    let repoKey = relativeName.replace(".js", "").toLowerCase();
    resources[repoKey] = resourcesFiles(filename)[key];
  });
});

export default {
  get(name) {
    const lowerCaseName = name.toLowerCase();
    return resources[lowerCaseName];
  },
};
