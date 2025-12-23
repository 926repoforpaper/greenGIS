import "@/wasm_exec.js";

async function getGQP(wasmData) {
  try {
    const go = new Go();
    const result = await WebAssembly.instantiate(wasmData, go.importObject);
    go.run(result.instance);

    const exports = Object.fromEntries(
      Object.entries(Go.exports).map(([name, fn]) => [name, wrapFunction(fn)])
    );

    return exports;
  } catch (error) {
    console.error("Error loading or instantiating wasm module:", error);
    throw error;
  }
}

function unwrapReturn(data) {
  if (!data) throw new Error("Unexpected response");
  if (data.error) throw new Error(data.error);
  return data.value;
}

function wrapFunction(fn) {
  return function (...args) {
    return unwrapReturn(fn(...args));
  };
}

export async function convertGeoParquetToGeoJSON(wasmData, parquetData) {
  try {
    const { fromParquet } = await getGQP(wasmData);
    return fromParquet(parquetData);
  } catch (error) {
    console.error("Error converting GeoParquet to GeoJSON:", error);
    throw error;
  }
}
