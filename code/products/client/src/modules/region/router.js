import regionFormDetail from "./components/regionFormDetail";

const routes = [
  {
    path: "/region-form/:id(\\d+)",
    name: "Region FormDetail",
    component: regionFormDetail,
    meta: {
      label: "t_region.headers.regionFormDetail",
    },
  },
];

export default routes;
