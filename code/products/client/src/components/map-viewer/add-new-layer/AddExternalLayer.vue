<template>
  <div class="flexContainer maxSpaceContainer">
    <v-row class="minSpaceContainer">
      <v-col cols="12">
        <v-form ref="formUrl" v-model="isValidUrl" class="formData">
          <v-row align="center" no-gutters>
            <!-- Text Field Column -->
            <v-col
              align-self="start"
              class="text-left"
              cols="12"
              :md="notSelect ? 3 : 4"
            >
              <v-text-field
                v-model="url"
                clearable
                dense
                :label="$t('addNewLayer.url')"
                outlined
                :rules="[(v) => !!v || $t('addNewLayer.urlRequired')]"
                @click:clear="clearData"
                @keydown.enter.prevent="searchLayers"
              ></v-text-field>
            </v-col>

            <!-- Filters Columns -->
            <v-col align-self="start" cols="12" md="2" offset-md="1">
              <v-select
                v-model="filteredService"
                outlined
                dense
                :items="resources"
                :label="$t('addNewLayer.prop.serviceType')"
                :menu-props="{ offsetY: true }"
                @change="
                  (value) => {
                    filteredService = value;
                  }
                "
                :rules="[(v) => !!v.length || $t('addNewLayer.valueRequired')]"
              ></v-select>
            </v-col>

            <v-col align-self="start" cols="12" md="1" offset-md="1">
              <v-switch
                dense
                :disabled="filteredService !== 'GeoTIFF'"
                v-model="mergeLayers"
                class="ma-0"
                :label="$t('addNewLayer.mergeLayers')"
                @change="searchLayers"
              ></v-switch>
            </v-col>

            <!-- Search Button Column -->
            <v-col
              align-self="start"
              cols="12"
              md="2"
              offset-md="1"
              class="searchButton"
            >
              <v-btn
                color="#6caac6"
                class="white--text"
                :loading="loadingUrl"
                @click="searchLayers"
              >
                <v-icon class="mr-1">mdi-magnify</v-icon>
                {{ $t("addNewLayer.search") }}
              </v-btn>
            </v-col>

            <v-col v-if="notSelect" align-self="start" cols="12" md="1">
              <v-btn class="appButton" @click="$emit('add-layer', newLayer)"
                ><v-icon class="mr-2"> mdi-plus </v-icon>
                {{ $t("addNewLayer.add") }}
              </v-btn>
            </v-col>
          </v-row>
        </v-form>
        <v-form ref="formLayer" v-model="isValidLayer" class="formData mt-3">
          <v-row
            v-if="(!loadingUrl && urlLayers != null) || urlServices != null"
            align="center"
            no-gutters
          >
            <!-- Service selection Column -->
            <v-col cols="12" md="4" v-if="!loadingUrl && urlServices != null">
              <v-select
                v-model="selectedUrlService"
                outlined
                dense
                :disabled="loadingLayer"
                :items="urlServices"
                :item-value="(item) => item"
                :item-text="(item) => item.serviceName"
                :label="$t('addNewLayer.selectService')"
                :menu-props="{ offsetY: true }"
                :rules="[(v) => !!v || $t('addNewLayer.serviceRequired')]"
                @change="selectService"
              >
              </v-select>
            </v-col>

            <!-- Layer selection Column -->
            <v-col
              cols="12"
              :md="urlServices != null ? 4 : 9"
              :offset-md="urlServices != null ? 1 : ''"
              v-if="!loadingUrl && urlLayers != null"
            >
              <v-select
                v-model="selectedUrlLayer"
                outlined
                dense
                multiple
                :items="urlLayers"
                :item-value="(item) => item"
                :item-text="(item) => item.layerTitle || item.layerName"
                :label="$t('addNewLayer.selectLayer')"
                :menu-props="{ offsetY: true }"
                :rules="[
                  (v) => (v && v.length > 0) || $t('addNewLayer.layerRequired'),
                ]"
                @change="createSelectedLayer"
              >
              </v-select>
            </v-col>

            <!-- Add Layer Button Column -->
            <v-col
              align-self="start"
              cols="12"
              md="2"
              offset-md="1"
              v-if="urlLayers != null"
            >
              <v-btn
                v-if="!notSelect"
                class="appButton"
                :loading="loadingLayer"
                :disabled="newLayer.length == 0"
                @click="$emit('add-layer', newLayer)"
                ><v-icon class="mr-2"> mdi-plus </v-icon>
                {{ $t("addNewLayer.add") }}
              </v-btn>
            </v-col>
          </v-row>
        </v-form>
      </v-col>
    </v-row>
    <v-row class="mt-4 maxSpaceContainer" no-gutters>
      <v-col cols="12">
        <map-preview
          v-if="newLayer.length > 0"
          id="extension"
          :entity="{ sampleGeom: null }"
          propName="extension"
          :newLayer="newLayer"
        ></map-preview>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import MapPreview from "@/components/map-viewer/add-new-layer/MapPreview.vue";
import { getUniqueLayerId } from "../common/map-layer-common";
import ResourceFactory from "../resources/ResourcesFactory";
import resources from "@/components/map-viewer/resources/resources.json";

let controller = new AbortController();

export default {
  name: "AddExternalLayer",
  props: ["map", "externalLayer"],
  components: {
    MapPreview,
  },
  data() {
    return {
      notSelect: false,
      url: this.externalLayer?.url || null,
      selectedUrlLayer: null,
      urlLayers: null,
      selectedUrlService: null,
      urlServices: null,
      newLayer: [],
      filteredService: this.externalLayer?.type || "WMS",
      isValidLayer: false,
      isValidUrl: false,
      layerName: null,
      loadingUrl: false,
      loadingLayer: false,
      resources: resources,
      mergeLayers: true,
      token: null,
    };
  },
  beforeDestroy() {
    controller.abort();
  },
  mounted() {
    controller = new AbortController();
    if (this.externalLayer?.url) {
      this.searchLayers();
    }
  },
  methods: {
    searchLayers() {
      this.notSelect = false;
      this.loadingUrl = true;
      if (this.isValidUrl) {
        this.searchResource();
      } else {
        this.$refs.formUrl.validate();
        this.loadingUrl = false;
      }
    },
    searchResource() {
      this.newLayer = [];
      const resource = ResourceFactory.get(this.filteredService);

      resource
        .search(
          this.url,
          controller,
          this.externalLayer?.name || null,
          this.mergeLayers
        )
        .then(async (result) => {
          if (result.layers) {
            this.urlLayers = result.layers;
            this.urlLayers.sort((a, b) =>
              a.layerName.localeCompare(b.layerName)
            );
            if (this.urlLayers.length === 0) {
              this._errorNoDataFound(this.$t("addNewLayer.noLayers"), true);
            }
          } else if (result.services) {
            this.urlServices = result.services;
            this.urlServices.sort((a, b) =>
              a.serviceName.localeCompare(b.serviceName)
            );
            if (this.urlServices.length === 0) {
              this._errorNoDataFound(this.$t("addNewLayer.noServices"), true);
            }
          } else if (Array.isArray(result)) {
            this.newLayer = await Promise.all(
              result.map(
                async (layer) =>
                  await this.createLayer(layer, {
                    added: true,
                    label: layer.name,
                  })
              )
            );
          } else {
            this.newLayer.push(
              await this.createLayer(result, {
                added: true,
                label: result.name,
              })
            );
          }
        })
        .catch(() => this._errorNoDataFound(this.$t("addNewLayer.noLayers")))
        .finally(() => {
          if (this.newLayer.length > 0) this.notSelect = true;
          this.loadingUrl = false;
        });
    },
    async selectService() {
      this.newLayer = [];
      this.selectedUrlLayer = [];
      const resource = ResourceFactory.get(this.filteredService);

      resource
        .getLayers(this.selectedUrlService.serviceUrl)
        .then((layers) => (this.urlLayers = layers))
        .catch(() => this._errorNoDataFound(this.$t("addNewLayer.noLayers")));
    },
    async createSelectedLayer() {
      // Return error if no layer is selected
      if (this.selectedUrlLayer.length === 0) {
        this.newLayer = [];
        this.$refs.formLayer.validate();
        return;
      }

      this.loadingLayer = true;
      const newLayerPromises = [];
      const token = Math.floor(Math.random() * 1000000000);
      this.token = token;

      this.selectedUrlLayer.forEach((selectedLayer) => {
        const createdNewLayer = this.newLayer.find(
          (layer) => layer.getId() === selectedLayer.layerName
        );

        if (!createdNewLayer) {
          const layer = {
            name: selectedLayer.layerName,
            url: selectedLayer.layerUrl || this.url,
            options: {
              layers: [selectedLayer.layerName],
              format: "image/png",
              transparent: true,
            },
          };

          const layerParams = {
            added: true,
            label: selectedLayer.layerTitle || selectedLayer.layerName,
          };

          newLayerPromises.push(this.createLayer(layer, layerParams));
        } else {
          newLayerPromises.push(createdNewLayer);
        }
      });

      Promise.all(newLayerPromises).then((newLayers) => {
        if (this.token == token) this.newLayer = newLayers;
        this.loadingLayer = false;
      });
    },
    async createLayer(layer, layerParams) {
      const resource = ResourceFactory.get(this.filteredService);
      let newLayer = await resource.create(layer, layerParams);

      if (this.map.getLayer(layer.name)) {
        console.warn("layer id already in use... generating new id");
        newLayer.options.id = getUniqueLayerId(this.map, layer.name);
      }

      return newLayer;
    },
    clearData() {
      controller.abort();
      controller = new AbortController();

      this.loadingUrl = false;
      this.url = null;
      this.urlLayers = null;
      this.urlServices = null;
      this.token = null;
      this.selectedUrlLayer = null;
      this.selectedUrlService = null;
      this.newLayer = [];
      this.notSelect = false;
      this.$emit("clear-external");
    },
    _errorNoDataFound(message, resetVariables = false) {
      if (resetVariables) {
        this.urlLayers = null;
        this.urlServices = null;
      }
      this.$notify({
        text: message,
        type: "error",
      });
    },
  },
};
</script>
<style scoped>
.minSpaceContainer {
  flex: 0;
}
.flexContainer {
  display: flex;
  flex-direction: column;
}
.maxSpaceContainer {
  flex: 1;
}
.searchButton {
  text-align: left;
  margin-bottom: 20px;
}
@media (min-width: 600px) {
  .searchButton {
    margin-bottom: 20px;
  }
}
</style>
