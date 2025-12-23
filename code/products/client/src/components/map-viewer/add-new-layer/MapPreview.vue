<template>
  <div class="maxHeight">
    <span>
      {{
        $t("addNewLayer.newLayers", {
          layersNumber: this.newLayer.length,
          layersNames: this.newLayer.map((layer) => layer.getId()).join(", "),
        })
      }}
    </span>
    <v-container
      class="containerMap maxHeight"
      :ref="id"
      :id="id"
    ></v-container>
  </div>
</template>

<script>
import initMap from "@/common/initMap";
import "leaflet.fullscreen";

export default {
  name: "MapPreview",
  props: {
    entity: {},
    propName: {},
    id: {
      required: false,
      type: String,
      default: "map",
    },
    newLayer: null,
  },
  data() {
    return {
      previewMap: null,
    };
  },
  mounted() {
    // setting leaflet map
    this.map = initMap(this.id, this.id);
    this.previewMap = this.map.getLeafletMap();

    // Full screen button
    L.control.fullscreen({ forceSeparateButton: true }).addTo(this.previewMap);

    // Add preview layer after map rendered
    this.previewMap.on("load", this.layerPreview);
  },
  watch: {
    newLayer() {
      this.layerPreview();
    },
  },
  methods: {
    async layerPreview() {
      if (this.newLayer.length == 0) {
        return;
      }

      // Remove previously added layers if not needed
      this.map.getOverlays().forEach((layer) => {
        if (!this.newLayer.find((newLayer) => newLayer == layer))
          this.map.removeLayer(layer.getId());
      });

      // Add MV layer
      this.newLayer.forEach((layer) => {
        if (!this.map.getLayer(layer.getId())) {
          layer.setSelected(true);
          this.map.addLayer(layer);
        }
      });

      // Center map to bounds
      const bounds = await this.newLayer[this.newLayer.length - 1].getBounds();
      if (bounds && bounds.isValid()) {
        this.map.centerView(bounds);
      }
    },
  },
};
</script>

<style scoped>
.container {
  height: 100%;
  width: auto;
  z-index: 3;
}
.maxHeight {
  height: 100%;
}
</style>
