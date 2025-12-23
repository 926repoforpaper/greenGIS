const { Builder, logging } = require("selenium-webdriver");
const chrome = require("selenium-webdriver/chrome");

async function getChromeWebDriver(headless = true) {
  return new Promise(async (resolve, reject) => {
    try {
      let options = new chrome.Options();

      if (headless) {
        options.addArguments(
          "--headless",
          "--no-sandbox",
          "--disable-dev-shm-usage",
          "--disable-gpu"
        );
      }

      options.addArguments("--lang=es");
      options.addArguments("start-maximized");
      options.addArguments("--incognito");

      let prefs = new logging.Preferences();
      prefs.setLevel(logging.Type.BROWSER, logging.Level.ALL);
      options.setLoggingPrefs(prefs);

      driver = await new Builder()
        .forBrowser("chrome")
        .setChromeOptions(options) // Aplicamos las opciones
        .build();

      await driver.manage().deleteAllCookies();

      resolve(driver);
    } catch (err) {
      console.error(err);
      reject(err);
    }
  });
}

async function getBrowserLogs(driver) {
  const logs = await driver.manage().logs().get(logging.Type.BROWSER);
  logs.forEach((log) => {
    const timestamp = new Date(log.timestamp).toISOString();
    console.log(`\x1b[33m[${timestamp}] Browser log: ${log.message}\x1b[0m`);
  });
}

async function monitorLogs(driver, duration = 30000) {
  const endTime = Date.now() + duration;
  while (Date.now() < endTime) {
    await getBrowserLogs(driver);
    await new Promise((resolve) => setTimeout(resolve, 2000)); // Wait 2 seconds
  }
}

module.exports = {
  getChromeWebDriver, monitorLogs
};
