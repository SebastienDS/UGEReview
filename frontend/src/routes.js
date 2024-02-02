import GoBack from "./components/GoBack.svelte";
import Home from "./components/Home.svelte";
import Test from "./components/Test.svelte";

export const routes = {
    "/": Home,
    "/test": Test,
    "*": GoBack
}