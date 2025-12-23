<template>
  <v-tooltip left open-delay="200" color="var(--appColor)">
    <template v-slot:activator="{ on: onMenu, attrs }">
      <v-menu
        content-class="layers-menu"
        class="menu"
        :close-on-content-click="false"
        offset-y
        ref="menu"
        transition="slide-y-transition"
        v-bind="attrs"
        v-if="map"
        v-model="isMenuOpen"
        v-on="onMenu"
      >
        <template v-slot:activator="{ on: onTooltip, attrs }">
          <v-btn
            color="white"
            v-bind="attrs"
            v-on="{ ...onMenu, ...onTooltip }"
          >
            <v-icon>mdi-layers-triple</v-icon>
          </v-btn>
        </template>
        <v-container
          flex
          class="pl-0 pr-0 white layers-container"
          :class="calcOverlays <= 0 ? 'pb-0' : 'pb-2'"
        >
          <v-col cols="12" class="title-container pa-0">
            <v-row class="title-row px-2 py-1 ml-0">
              <v-col cols="1" class="text-center">
                <v-btn
                  v-if="layersShown"
                  x-small
                  icon
                  @click="changeVisibilityLayers(false)"
                >
                  <v-icon color="white">mdi-eye</v-icon>
                </v-btn>
                <v-btn v-else x-small icon @click="changeVisibilityLayers">
                  <v-icon color="white">mdi-eye-off</v-icon>
                </v-btn>
              </v-col>
              <v-col class="text-center" cols="3">
                {{ $t("layerManager.name") }}
              </v-col>
              <v-col class="text-center" cols="3">
                {{ $t("layerManager.style") }}
              </v-col>
              <v-col class="text-center" cols="2">
                {{ $t("layerManager.center") }}/{{ $t("layerManager.order") }}
              </v-col>
              <v-col class="text-center" cols="2">
                {{ $t("layerManager.opacity") }}
              </v-col>
            </v-row>
          </v-col>

          <v-col class="rows-container pa-0" v-if="!loadingMap">
            <v-row
              align="center"
              class="overlay-row"
              v-for="layer in individualLayers"
              :key="layer.id"
            >
              <individual-layer
                :layer="layer"
                :calc-overlays="calcOverlays"
                :new-layer-name="newLayerName"
                :layer-being-edited="layerBeingEdited"
                :show-style-dialog="showStyleDialog"
              />
            </v-row>
            <v-row
              v-if="calcOverlays <= 0"
              class="overlay-row"
              justify="center"
            >
              {{ $t("mapViewer.addALayer") }}&nbsp;
              <a href="#" @click.prevent="addLayer">{{
                $t("mapViewer.addALayer-link")
              }}</a>
            </v-row>

            <wms-new-style
              :showStyleDialog="showStyleDialog"
              :geometryTypeLayer="geometryTypeLayer"
              @close="
                (showStyleDialog = false),
                  (layerBeingEdited = false),
                  (geometryTypeLayer = null)
              "
              @update="createCustomStyle"
            ></wms-new-style>
          </v-col>
        </v-container>
      </v-menu>
    </template>
    <span>{{ $t("layerManager.title") }}</span>
  </v-tooltip>
</template>

<script>
import maps from "@/components/map-viewer/config-files/maps.json";
import IndividualLayer from "./IndividualLayer.vue";
import { createWMSStyle } from "@/components/map-viewer/common/map-styles-common";
import WMSNewStyle from "./WMSNewStyle.vue";

export default {
  provide() {
    return {
      getLayer: this.getLayer,
      changeSelectedLayer: this.changeSelectedLayer,
      changeOpacity: this.changeOpacity,
      layerOrderDown: this.layerOrderDown,
      layerOrderUp: this.layerOrderUp,
      setCenterOnLayer: this.setCenterOnLayer,
      changeLayerColor: this.changeLayerColor,
      changeLayerStyle: this.changeLayerStyle,
      newStyleLayers: this.newStyleLayers,
      getLayerType: this.getLayerType,
      changeExternal: this.changeExternal,
      isExternalLayer: this.isExternalLayer,
      editExternal: this.editExternal,
      deleteExternal: this.deleteExternal,
      wmsLegendSelected: this.wmsLegendSelected,
    };
  },
  components: {
    IndividualLayer,
    "wms-new-style": WMSNewStyle,
  },
  props: {
    value: {
      type: String,
      required: false,
    },
    map: {
      type: Object,
      required: true,
    },
    overlays: {
      type: Array,
      required: false,
    },
    multiple: {
      type: Boolean,
      required: false,
      default: false,
    },
    loadingMap: {
      type: Boolean,
      required: false,
    },
    buildWMSLegendControl: {
      type: Function,
      required: false,
      default: () => {},
    },
  },
  data() {
    return {
      isMenuOpen: false,
      calcOverlays: [],
      isDataLoaded: false,
      newLayerName: null,
      showStyleDialog: false,
      geometryTypeLayer: null,
      layerBeingEdited: null,
    };
  },
  watch: {
    isMenuOpen(newVal) {
      this.$emit("menu-open", newVal);

      if (newVal) {
        this.layerBeingEdited = null;
        if (!this.isDataLoaded) {
          this.calculateOverlays();
        }
      }
    },
    loadingMap(newVal) {
      if (!newVal) this.calculateOverlays();
    },
  },
  computed: {
    layersShown() {
      return this.calcOverlays.filter((layer) => layer.selected).length > 0;
    },
    individualLayers() {
      return this.calcOverlays.filter((item) => !item.group);
    },
  },
  mounted() {
    this.layerManagerName = maps.maps.find(
      (map) => map.name === this.map.getId()
    ).layerManager;
    this.$on("reload-layers", this.reloadShownLayers);
    this.calculateOverlays();
  },
  methods: {
    calculateOverlays() {
      const layers = this.map.getLayers().filter((el) => !el.options.baseLayer);
      const groupedOverlays = {};

      layers.forEach((layer, index) => {
        const layerData = this.extractLayerData(layer, index);
        this.addLayerWithoutGroup(groupedOverlays, layerData);
      });

      this.calcOverlays = Object.values(groupedOverlays);
      this.calcOverlays.sort((a, b) => a.order - b.order);
    },
    extractLayerData(layer, index) {
      return {
        id: layer.options.id,
        name: layer.options.label,
        selected: layer.options.selected,
        slider: layer.options.opacity * 100,
        order: index,
        centered: false,
        styles: layer.getAvailableStyles(),
        selectedStyle: layer.getStyle(),
      };
    },
    addLayerWithoutGroup(groupedOverlays, layerData) {
      if (!groupedOverlays[layerData.name]) {
        groupedOverlays[layerData.name] = layerData;
      }
    },
    reloadShownLayers() {
      this.calcOverlays
        .filter((layer) => layer.selected)
        .forEach((layer) => {
          this.changeSelectedLayer(layer, true);
        });
    },
    changeLayerStyle(newStyle, layerSelected) {
      const realLayer = this.getLayer(layerSelected);
      realLayer.setStyle(newStyle);
    },
    changeLayerColor(newColor, layerSelected) {
      const realLayer = this.getLayer(layerSelected);
      realLayer.setColor(newColor.target.value);
    },
    changeOpacity(newOpacity, layerSelected) {
      const realLayer = this.getLayer(layerSelected);
      realLayer.setOpacity(newOpacity);
    },
    changeSelectedLayer(layerSelected, newVal) {
      const realLayer = this.getLayer(layerSelected);
      if (!newVal) {
        layerSelected.selected = false;
        this.map.hideLayer(realLayer);
      } else {
        layerSelected.selected = true;
        this.map.showLayer(realLayer);
      }
    },
    setCenterOnLayer(layerSelected) {
      // If layer was already selected, we center on all layers
      if (layerSelected.centered) {
        this.calcOverlays = this.calcOverlays.map((el) => {
          el.centered = false;
          return el;
        });
        this.map.centerView("visible");
        return;
      }

      // Else, set center with the layer bounds
      const realLayer = this.getLayer(layerSelected);
      realLayer.getBounds().then((bounds) => {
        this.map.centerView(bounds);
        // Change state and style
        this.calcOverlays = this.calcOverlays.map((el) => {
          if (el.centered) el.centered = false;
          if (el.id === layerSelected.id) el.centered = true;
          return el;
        });
      });
    },
    // Sets the order of a layer to a higher value
    layerOrderUp(layer) {
      if (layer.order === 0) return;
      const index = layer.order;

      // Set new order on layer list
      this.calcOverlays[index].order = this.calcOverlays[index].order - 1;
      this.calcOverlays[index - 1].order =
        this.calcOverlays[index - 1].order + 1;

      // Set new order on map
      this.getLayer(layer).setOrder(layer.order);
      this.getLayer(this.calcOverlays[index - 1]).setOrder(layer.order + 1);

      // Re-render list sorted
      this.calcOverlays = this.calcOverlays.sort((a, b) => a.order - b.order);
    },
    // Sets the order of a layer to a lower value
    layerOrderDown(layer) {
      if (layer.order === this.calcOverlays.length - 1) return;

      const index = layer.order;
      // Set new order on layer list
      this.calcOverlays[index].order = this.calcOverlays[index].order + 1;
      this.calcOverlays[index + 1].order =
        this.calcOverlays[index + 1].order - 1;

      // Set new order on map
      this.getLayer(layer).setOrder(layer.order);
      this.getLayer(this.calcOverlays[index + 1]).setOrder(layer.order - 1);

      // Re-render list sorted
      this.calcOverlays = this.calcOverlays.sort((a, b) => a.order - b.order);
    },
    changeExternal(layer) {
      this.layerBeingEdited = layer.id;
      this.newLayerName = layer.name;
    },
    editExternal(layer) {
      if (!this.newLayerName) return;

      this.getLayer(layer).setLabel(this.newLayerName);

      this.calculateOverlays();
      this.newLayerName = null;
      this.layerBeingEdited = null;
    },
    deleteExternal(layer) {
      this.map.removeLayer(layer.id);

      this.calculateOverlays();
    },
    changeVisibilityLayers(visible = true) {
      this.calcOverlays.forEach((item) => {
        if (item.group) {
          this.changeSelectedGroup(item.group, visible);
        } else {
          this.changeSelectedLayer(item, visible);
        }
      });
    },
    getLayer(layerSelected) {
      return this.map
        .getLayers()
        .find((layer) => layer.options.id === layerSelected.id);
    },
    getLayerType(layerSelected) {
      const layerFound = this.getLayer(layerSelected);

      return layerFound.getType();
    },
    isExternalLayer(layerSelected) {
      const findLayer = this.getLayer(layerSelected);

      return !!findLayer.options.added;
    },
    wmsLegendSelected(layer) {
      this.$emit("wms-legend-selected", layer);
    },
    async newStyleLayers(layerSelected) {
      this.layerBeingEdited = layerSelected;
      const realLayer = this.map.getLayer(layerSelected);
      this.getGeometryType(realLayer).then((response) => {
        this.geometryTypeLayer = response;
        this.showStyleDialog = true;
      });
    },
    async createCustomStyle({
      styleName,
      fillColor,
      strokeColor,
      fillOpacity,
      strokeOpacity,
      geometryType,
    }) {
      const realLayer = this.map.getLayer(this.layerBeingEdited);
      const opacity = realLayer.getOpacity();

      // Create new custom style
      const customStyle = createWMSStyle({
        name: styleName,
        fillColor: fillColor,
        fillOpacity: fillOpacity,
        strokeColor: strokeColor,
        strokeOpacity: strokeOpacity,
        type: "WMSLayerStyle",
        geometryType: geometryType,
      });

      // Update layer to apply custom style
      const index = realLayer
        .getAvailableStyles()
        .findIndex((style) => style.id === styleName);

      // Replace style if already exists, add if not
      if (index === -1) {
        realLayer.getAvailableStyles().push(customStyle);
      } else {
        realLayer.getAvailableStyles()[index] = customStyle;
      }

      this.layerBeingEdited = null;
      this.showStyleDialog = false;
      this.geometryTypeLayer = null;
      realLayer.setStyle(styleName);
      realLayer.setOpacity(opacity);
      this.calculateOverlays();
    },
    async getGeometryType(realLayer) {
      const url = realLayer.options.url;
      const layerString = realLayer.options.params.layers[0];

      const xmlDoc = await fetch(
        new URL(url + "?service=WMS&version=1.1.1&request=GetCapabilities")
      )
        .then((response) => response.text())
        .then((data) => {
          return new DOMParser().parseFromString(data, "application/xml");
        });

      const layer = Array.from(
        xmlDoc.querySelectorAll(`Capability Layer Layer`)
      ).find((layer) => layer.innerHTML.includes(layerString));

      return layer.querySelector("Style > Name").textContent;
    },
    addLayer() {
      this.$refs.menu.save();
      this.$emit("add-layer");
    },
  },
};
</script>
<style scoped>
.layers-menu {
  max-width: 40% !important;
  right: 2vh !important;
  top: 5vh !important;
  left: initial !important;
}

body {
  padding: 0;
  margin: 0;
  max-width: 50%;
}

.layers-container {
  overflow-x: auto;
  padding-top: 0;
  margin-top: 0;
}

.title-container {
  overflow-x: hidden;
  min-width: 540px;
}

.title-row {
  margin-top: 0;
  padding-top: 0;
  margin-bottom: 1px;
  position: sticky;
  top: 0;
  background-color: #1976d2;
  opacity: 1;
  z-index: 100;
  font-size: small;
  color: white;
  box-shadow: 0 2px 2px -1px rgba(0, 0, 0, 0.4);
}

.col-visibility {
  padding-left: 11px;
}

.rows-container {
  overflow-y: auto;
  overflow-x: hidden;
  max-height: 50vh;
  padding-bottom: 0.75em;
}

.overlay-row {
  margin: 0 0.8em;
}

.overlay-col {
  margin: 0;
  padding: 0;
  padding-top: 5px;
}

.layer-selector {
  float: right;
  margin-right: 2px;
  padding: 0;
  font-size: small;
  max-width: 70px;
}

.color-selector {
  float: right;
  margin-right: 2px;
  padding-top: 6px;
  max-width: 40%;
}

.v-text-field {
  font-size: small;
}

.overlay-style-col {
  font-size: small;
}

::v-deep .v-messages {
  display: none;
}

::v-deep .v-label {
  font-size: small;
}

::v-deep .v-slider__thumb-label {
  position: absolute;
}
</style>
