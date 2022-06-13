import Endpoint from "../class/Endpoint";

export default () => [
    {
      id: `top`,
      color: "green",
      orientation: [0, -1],
      pos: [0.5, 0],
      Class: Endpoint
    },
    {
      id: `bottom`,
      color: "green",
      orientation: [0, 1],
      pos: [0.5, 0],
      Class: Endpoint
    },
  
];
