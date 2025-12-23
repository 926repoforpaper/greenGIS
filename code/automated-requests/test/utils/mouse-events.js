async function dispatchMouseEvent(driver, map, type, x, y) {
  return driver.executeScript(
    (map, type, x, y) => {
      const event = new MouseEvent(type, {
        clientX: x,
        clientY: y,
        bubbles: true,
        cancelable: true,
        view: window,
        button: 0,
      });
      map.dispatchEvent(event);
    },
    map,
    type,
    x,
    y
  );
}

module.exports = { dispatchMouseEvent };
