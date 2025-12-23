<template>
  <v-container fluid id="controls-container">
    <div v-if="map" class="map-controls">
      <div class="column">
        <v-btn
          v-if="$vuetify.breakpoint.smAndDown"
          color="white"
          max-height="25"
          max-width="20"
          @click.stop="showButtons"
        >
          <div v-if="!showBtns && $vuetify.breakpoint.smAndDown">
            <v-icon>mdi-wrench-outline</v-icon>
            <v-icon>mdi-chevron-down</v-icon>
          </div>
          <div v-else>
            <v-icon>mdi-wrench-outline</v-icon>
            <v-icon>mdi-chevron-up</v-icon>
          </div>
        </v-btn>
      </div>

      <div v-show="showBtns || !$vuetify.breakpoint.smAndDown">
        <div class="column">
          <layer-manager
            :loadingMap="loadingMap"
            :overlays="overlays"
            :map="map"
            @add-layer="openAddLayerDialog"
            @wms-legend-selected="
              (newVal) => (
                (dialogComponent = 'wms-legend'),
                (wmsLegendSelected = newVal),
                (showDialog = true)
              )
            "
          ></layer-manager>
        </div>

        <div class="column">
          <v-tooltip left open-delay="200" color="var(--appColor)">
            <template v-slot:activator="{ on, attrs }">
              <v-btn
                v-bind="attrs"
                v-on="on"
                color="white"
                @click.stop="openAddLayerDialog"
              >
                <v-icon>mdi-layers-plus</v-icon>
              </v-btn>
            </template>
            <span>{{ $t("addNewLayer.addNewLayerTitle") }}</span>
          </v-tooltip>
        </div>

        <div class="column">
          <v-tooltip left open-delay="200" color="var(--appColor)">
            <template v-slot:activator="{ on, attrs }">
              <v-btn
                v-bind="attrs"
                v-on="on"
                color="white"
                @click.stop="
                  (dialogComponent = 'change-base-layer'), (showDialog = true)
                "
              >
                <v-icon>mdi-layers-edit</v-icon>
              </v-btn>
            </template>
            <span>{{ $t("changeBaseLayer.changeBaseLayerTitle") }}</span>
          </v-tooltip>
        </div>
      </div>
    </div>
    <v-dialog
      v-model="showDialog"
      hide-overlay
      :width="1200"
      @click:outside="closeDialog"
    >
      <component
        v-if="dialogComponent"
        v-bind:is="dialogComponent"
        :map="map"
        :wmsLegend="wmsLegendSelected"
        @layer-added="(val) => $emit('map-updated', val)"
        @close="closeDialog"
      ></component>
    </v-dialog>
  </v-container>
</template>

<script>
import LayerManager from "./LayerManager.vue";

import ChangeBaseLayer from "../change-base-layer/ChangeBaseLayer.vue";

import AddNewLayer from "../add-new-layer/AddNewLayer.vue";

import WMSLegend from "../wms-legend/WMSLegend.vue";

export default {
  name: "RightMapControls",
  components: {
    LayerManager,

    "change-base-layer": ChangeBaseLayer,

    "add-new-layer": AddNewLayer,
    "wms-legend": WMSLegend,
  },
  props: {
    overlays: {
      type: Array,
      default: () => [],
    },
    map: {
      type: Object,
      default: () => {},
    },
    loadingMap: {
      type: Boolean,
      required: false,
    },
  },
  data() {
    return {
      showBtns: false,
      dialogComponent: null,
      showDialog: false,
      wmsLegendSelected: null,
    };
  },
  methods: {
    showButtons() {
      this.showBtns = !this.showBtns;
    },
    closeDialog() {
      this.dialogComponent = null;
      this.showDialog = false;
      this.wmsLegendSelected = null;
    },
    openAddLayerDialog() {
      this.dialogComponent = "add-new-layer";
      this.showDialog = true;
    },
  },
};
</script>

<style scoped>
.map-controls {
  position: absolute;
  z-index: 1000;
  top: 4px;
  right: 0px;
  margin-right: 0px;
  padding: 0;
}
.column {
  margin-top: 0.6em;
  padding-right: 8px;
}

/* we will explain what these classes do next! */
.v-enter-active,
.v-leave-active {
  transition: opacity 0.4s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}

.btn-selected {
  border: 2px solid #1976d2;
}

.arrow-icon {
  font-size: 24px;
}
</style>
