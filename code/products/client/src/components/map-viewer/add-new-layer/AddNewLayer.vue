<template>
  <v-card class="card" height="calc(100vh - 100px)">
    <v-card-title primary-title class="headline card-title white--text">
      <span>
        {{ $t("addNewLayer.addNewLayerTitle") }}
      </span>
      <v-spacer></v-spacer>
      <v-btn @click="close" color="white" icon>
        <v-icon>mdi-close</v-icon>
      </v-btn>
    </v-card-title>
    <v-card-text class="flexContainer">
      <v-row no-gutters class="minSpaceContainer">
        <v-radio-group v-model="radios" row mandatory>
          <!-- Layer from URL -->
          <v-radio
            class="radio"
            name="radio1"
            :label="$t('addNewLayer.addUrlLayer')"
            value="0"
            @click="clearData"
          ></v-radio>

          <!-- Layer from layers.json -->
          <v-radio
            class="radio"
            name="radio2"
            :label="$t('addNewLayer.addExistentLayer')"
            value="1"
            @click="clearData"
          ></v-radio>
        </v-radio-group>
      </v-row>

      <v-divider class="mb-4"></v-divider>

      <v-row no-gutters class="maxSpaceContainer">
        <v-col cols="12" class="flexContainer">
          <!-- Layer from URL -->
          <v-container
            v-if="radios == '0'"
            class="flexContainer maxSpaceContainer"
          >
            <AddExternalLayer
              :map="map"
              :externalLayer="layer"
              @add-layer="addLayer"
              @clear-external="clearData"
            >
            </AddExternalLayer>
          </v-container>

          <!-- Layer from layers.json -->
          <v-container v-if="radios == '1'">
            <AddFilesLayer :map="map" @add-layer="addLayer"> </AddFilesLayer>
          </v-container>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script>
import AddExternalLayer from "./AddExternalLayer.vue";
import AddFilesLayer from "./AddFilesLayer.vue";

export default {
  name: "AddNewLayerControl",
  props: ["map"],
  components: {
    AddExternalLayer,
    AddFilesLayer,
  },
  data() {
    return {
      radios: null,
      layer: null,
    };
  },
  methods: {
    close() {
      this.$emit("close");
      this.$destroy();
    },
    clearData() {
      this.layer = null;
    },
    addLayer(layers) {
      if (layers.length == 0) return;

      layers.forEach((layer) => {
        if (layer.getMap()) {
          layer.getMap().removeLayer(layer.getId());
          layer.setSelected(true);
        }

        this.map.addLayer(layer);
        this.$emit("layer-added", layer.options.id);
      });

      this.close();
    },
    searchFromUrlCatalog(item) {
      this.radios = "0";
      this.layer = {
        url: item.url,
        type: item.type,
        name: item.name || null,
      };
    },
  },
};
</script>
<style scoped>
.minSpaceContainer {
  flex: 0;
}
.flexContainer {
  height: 90%;
  display: flex;
  flex-direction: column;
}
.maxSpaceContainer {
  flex: 1;
}
.card-title {
  background-color: #1976d2;
  position: sticky;
  top: 0;
  z-index: 1;
}
.radio {
  font-size: 0.9em;
}
</style>
