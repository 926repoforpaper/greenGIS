<template>
  <v-col class="py-2" cols="12">
    <v-row class="px-3" style="align-items: center">
      <v-col class="overlay-col" cols="4">
        <v-checkbox
          class="mt-1 pt-0"
          :label="layer.id"
          :input-value="layer.selected"
          :readonly="layerBeingEdited === layer.id"
          @change="
            (val) =>
              layerBeingEdited === layer.id
                ? null
                : changeSelectedLayer(layer, val)
          "
        >
          <template v-slot:label>
            <div v-if="layerBeingEdited === layer.id && !showStyleDialog">
              <v-text-field
                :label="$t('layerManager.editLayer')"
                outlined
                dense
                type="input"
                :value="newLayerName"
                :rules="[(v) => !!v || $t('layerManager.nameRequired')]"
                @keydown.enter="editExternal(layer)"
              ></v-text-field>
            </div>
            <div v-else>{{ layer.name }}</div>
          </template>
        </v-checkbox>
      </v-col>

      <v-col class="text-center" cols="3">
        <div v-if="getLayerType(layer) === 'WMS'">
          <v-select
            class="layer-selector"
            hide-details
            dense
            :items="layer.styles"
            item-text="id"
            :label="
              layer.selectedStyle == null ? $t('layerManager.style') : null
            "
            :menu-props="{ closeOnContentClick: true }"
            :value="layer.selectedStyle"
            @change="(val) => changeLayerStyle(val, layer)"
          >
            <template v-slot:append-item>
              <v-divider></v-divider>
              <v-list-item
                v-for="item in [{ id: $t('layerManager.wms-style.newStyle') }]"
                :key="item.id"
                @click="newStyleLayers(layer.id)"
              >
                <v-icon color="black" small>mdi-plus</v-icon>
                <v-list-item-title>{{ item.id }}</v-list-item-title>
              </v-list-item>
            </template>
          </v-select>
        </div>
        <div v-else-if="getLayerType(layer) === 'GEOJSON'">
          <input
            class="color-selector"
            type="color"
            :value="getLayer(layer).getColor()"
            @change="(val) => changeLayerColor(val, layer)"
          />
        </div>
      </v-col>

      <v-col class="text-center" cols="3">
        <v-btn small icon @click="setCenterOnLayer(layer)">
          <v-icon :color="layer.centered ? 'primary' : ''">
            mdi-crosshairs-gps
          </v-icon>
        </v-btn>
        <v-btn small icon @click="wmsLegendSelected(layer)">
          <v-icon>mdi-map-legend</v-icon>
        </v-btn>
        <v-btn
          small
          icon
          :disabled="layer.order === 0"
          @click="layerOrderUp(layer)"
        >
          <v-icon> mdi-arrow-up </v-icon>
        </v-btn>
        <v-btn
          small
          icon
          :disabled="layer.order === calcOverlays.length - 1"
          @click="layerOrderDown(layer)"
        >
          <v-icon> mdi-arrow-down</v-icon>
        </v-btn>
        <v-tooltip
          v-if="isExternalLayer(layer)"
          left
          open-delay="200"
          color="var(--appColor)"
        >
          <template v-slot:activator="{ on }">
            <v-btn
              v-if="layerBeingEdited === layer.id"
              v-on="on"
              small
              icon
              @click="editExternal(layer)"
            >
              <v-icon> save </v-icon>
            </v-btn>
            <v-btn v-else v-on="on" small icon @click="changeExternal(layer)">
              <v-icon> edit </v-icon>
            </v-btn>
          </template>
          <span>{{ $t("layerManager.edit") }}</span>
        </v-tooltip>
        <v-tooltip
          v-if="isExternalLayer(layer)"
          left
          open-delay="200"
          color="var(--appColor)"
        >
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" small icon @click="deleteExternal(layer)">
              <v-icon>mdi-delete</v-icon>
            </v-btn>
          </template>
          <span>{{ $t("layerManager.delete") }}</span>
        </v-tooltip>
      </v-col>

      <v-col class="ma-0 pa-0 text-center" cols="2">
        <v-slider
          v-bind:value="layer.slider"
          min="0"
          max="100"
          thumb-label
          @change="(val) => changeOpacity(val, layer)"
        ></v-slider>
      </v-col>
    </v-row>
  </v-col>
</template>

<script>
export default {
  props: [
    "layer",
    "calcOverlays",
    "newLayerName",
    "layerBeingEdited",
    "showStyleDialog",
  ],
  inject: [
    "getLayer",
    "changeSelectedLayer",
    "changeOpacity",
    "layerOrderDown",
    "layerOrderUp",
    "setCenterOnLayer",
    "changeLayerColor",
    "changeLayerStyle",
    "newStyleLayers",
    "getLayerType",
    "changeExternal",
    "isExternalLayer",
    "editExternal",
    "deleteExternal",
    "wmsLegendSelected",
  ],
};
</script>
<style scoped>
.overlay-col {
  margin: 0;
  padding: 0;
  padding-top: 5px;
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
