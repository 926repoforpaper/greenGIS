import townFormDetail from "./components/townFormDetail";

const routes = [
  {
    path: "/town-form/:id(\\d+)",
    name: "Town FormDetail",
    component: townFormDetail,
    meta: {
      label: "t_town.headers.townFormDetail",
    },
  },
];

export default routes;
