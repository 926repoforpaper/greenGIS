import provinceFormDetail from "./components/provinceFormDetail";

const routes = [
  {
    path: "/province-form/:id(\\d+)",
    name: "Province FormDetail",
    component: provinceFormDetail,
    meta: {
      label: "t_province.headers.provinceFormDetail",
    },
  },
];

export default routes;
