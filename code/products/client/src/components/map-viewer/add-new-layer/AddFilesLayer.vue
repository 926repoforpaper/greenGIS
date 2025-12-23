<template>
  <div>
    <v-row align="center" no-gutters>
      <v-col class="text-left">
        <v-autocomplete
          v-model="layerName"
          outlined
          dense
          :items="layersFromJSON"
          item-text="name"
          item-value="id"
          :label="$t('addNewLayer.selectLayer')"
          :menu-props="{ offsetY: true }"
          :rules="[(v) => !!v || $t('addNewLayer.layerRequired')]"
          @change="createLayer"
        >
          <template v-slot:item="{ item }">
            {{ $t("mapViewer.layer-label." + item.id) }}
          </template>
          <template v-slot:selection="{ item }">
            {{ $t("mapViewer.layer-label." + item.id) }}
          </template>
        </v-autocomplete>
      </v-col>
      <v-col align-self="start" cols="12" md="3" offset-md="1">
        <v-btn class="appButton" @click="$emit('add-layer', newLayer)"
          ><v-icon class="mr-2"> mdi-plus </v-icon>
          {{ $t("addNewLayer.add") }}
        </v-btn>
      </v-col>
    </v-row>
    <v-row class="mt-4" no-gutters>
      <v-col cols="12">
        <map-preview
          v-show="newLayer.length > 0"
          id="extension"
          height="250px"
          :entity="{ sampleGeom: null }"
          propName="extension"
          :newLayer="newLayer"
        ></map-preview>
      </v-col>
    </v-row>
  </div>
</template>
<script>
import layerList from "@/components/map-viewer/config-files/layers.json";
import MapPreview from "@/components/map-viewer/add-new-layer/MapPreview.vue";
import {
  createGeoJSONLayer,
  createWMSLayer,
  getUniqueLayerId,
} from "../common/map-layer-common";

export default {
  name: "AddFilesLayer",
  props: ["map"],
  components: {
    MapPreview,
  },
  data() {
    return {
      isValidLayer: false,
      layersFromJSON: [],
      layerName: false,
      newLayer: [],
    };
  },
  mounted() {
    layerList.layers.forEach((layer) => {
      if (layer.layerType !== "tilelayer") {
        this.layersFromJSON.push({
          id: layer.name,
          name: this.$t("mapViewer.layer-label." + layer.name),
        });
      }
    });

    // Sorting layers based on i18n
    this.layersFromJSON.sort((a, b) => {
      const renamedA = this.$t("mapViewer.layer-label." + a.id);
      const renamedB = this.$t("mapViewer.layer-label." + b.id);
      return renamedA.localeCompare(renamedB);
    });
  },
  methods: {
    createLayer() {
      // Return error if no layer is selected
      if (this.layerName == null) {
        this.$refs.formLayer.validate();
        return;
      }

      const layerParams = {
        added: true,
      };

      if (this.layerName != null) {
        const layer = layerList.layers.find(
          (layer) => layer.name === this.layerName
        );

        layerParams.label = this.$t(
          "mapViewer.layer-label." + layer.name.replace(".", "-")
        );

        let newLayer = null;
        if (layer.layerType === "wms") {
          newLayer = createWMSLayer(layer, layerParams);
        } else if (layer.layerType === "geojson") {
          newLayer = createGeoJSONLayer(layer, layerParams);
        }

        if (this.map.getLayer(this.layerName)) {
          console.warn("layer id already in use... generating new id");
          newLayer.options.id = getUniqueLayerId(this.map, this.layerName);
        }

        this.newLayer.push(newLayer);
      }
    },
  },
};
</script>
