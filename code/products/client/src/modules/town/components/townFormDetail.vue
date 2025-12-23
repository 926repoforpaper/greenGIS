<template>
  <v-container>
    <v-card v-if="!isLoading">
      <v-card-title>
        <v-row align="center" justify="space-between" no-gutters>
          <v-col class="d-none d-md-block">
            <span class="headline no-split-words">
              {{ $t($route.meta.label) }}
            </span>
          </v-col>

          <v-col class="text-right">
            <v-btn @click="back()">
              <v-icon>arrow_back</v-icon>
              <span class="d-none d-sm-block"> {{ $t("back") }} </span>
            </v-btn>
          </v-col>
        </v-row>
      </v-card-title>
      <v-card-text>
        <v-row dense>
          <v-col cols="12" md="6" class="d-flex" style="flex-direction: column">
            <v-row align="center" dense>
              <v-col cols="3" md="2" class="text-left font-weight-bold">
                {{ $t("t_town.prop.id") }}:
              </v-col>
              <v-col cols="9" md="10">
                {{ entity.id }}
              </v-col>
            </v-row>
            <v-row align="center" dense>
              <v-col cols="3" md="2" class="text-left font-weight-bold">
                {{ $t("t_town.prop.name") }}:
              </v-col>
              <v-col cols="9" md="10">
                {{ entity.name }}
              </v-col>
            </v-row>
            <v-row align="center" dense>
              <v-col cols="3" md="2" class="text-left font-weight-bold">
                {{ $t("t_town.prop.code") }}:
              </v-col>
              <v-col cols="9" md="10">
                {{ entity.code }}
              </v-col>
            </v-row>
            <v-row align="center" dense>
              <v-col cols="3" md="2" class="text-left font-weight-bold">
                {{ $t("t_town.prop.population") }}:
              </v-col>
              <v-col cols="9" md="10">
                {{ entity.population }}
              </v-col>
            </v-row>
            <v-row align="center" dense>
              <v-col cols="3" md="2" class="text-left font-weight-bold">
                {{ $t("t_town.prop.populationDate") }}:
              </v-col>
              <v-col cols="9" md="10">
                {{ entity.populationDate | dateFormat }}
              </v-col>
            </v-row>
          </v-col>
          <v-col cols="12" md="6" class="d-flex" style="flex-direction: column">
            <v-row align="center" dense>
              <v-col cols="3" md="2" class="text-left font-weight-bold">
                {{ $t("t_town.prop.location") }}:
              </v-col>
              <v-col cols="9" md="10">
                <map-field
                  id="location"
                  v-bind:entity="entity"
                  :editable="false"
                  height="400px"
                  propName="location"
                  entityName="town"
                ></map-field>
              </v-col>
            </v-row>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
    <loading-page v-if="isLoading"></loading-page>
  </v-container>
</template>

<script>
import checkInvalidID from "@/common/checkInvalidID";

import MapField from "@/components/map-field/MapField.vue";
import LoadingPage from "@/components/loading-page/LoadingPage.vue";

import RepositoryFactory from "@/repositories/RepositoryFactory";
const TownEntityRepository = RepositoryFactory.get("TownEntityRepository");

export default {
  name: "townFormDetail",
  components: { LoadingPage, MapField },
  data() {
    return {
      loading: false,
      entity: null,
    };
  },
  computed: {
    isLoading() {
      return this.loading;
    },
  },
  beforeRouteUpdate(to, from, next) {
    this._fetchData(to.params.id);
    next();
  },
  created() {
    this._fetchData(this.$route.params.id);
  },
  methods: {
    _fetchData(id) {
      this.loading = true;

      return TownEntityRepository.get(id)
        .then((res) => (this.entity = res))
        .catch((err) => checkInvalidID(err))
        .finally(() => (this.loading = false));
    },
    back() {
      this.$router.push({ name: "Home", params: { backAction: true } });
    },
  },
};
</script>
