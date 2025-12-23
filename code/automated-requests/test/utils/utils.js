const path = require('path');
const fs = require("fs");

async function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

async function getMapDetails(driver) {
  return driver.executeScript(() => window.mapDetails);
}

function createLogger() {
  const dateStr = new Date().toISOString().split('T')[0];
  const logFilePath = path.join(__dirname, `execution_log.csv`);
  const stream = fs.createWriteStream(logFilePath, { flags: 'a' });
  return function logWithTimestamp(message) {
    const timestampIso = new Date().toISOString();
    const timestampMs = Date.now();
    const logEntry = `${timestampIso},${timestampMs},${message}\n`;

    stream.write(logEntry);
    console.log(logEntry.trim());
  };
}


module.exports = {
  sleep,
  getMapDetails,
  createLogger,
};
