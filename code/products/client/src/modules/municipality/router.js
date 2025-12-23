import municipalityFormDetail from "./components/municipalityFormDetail";

const routes = [
  {
    path: "/municipality-form/:id(\\d+)",
    name: "Municipality FormDetail",
    component: municipalityFormDetail,
    meta: {
      label: "t_municipality.headers.municipalityFormDetail",
    },
  },
];

export default routes;
