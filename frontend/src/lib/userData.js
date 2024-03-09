import { browser } from "$app/environment";
import { get, writable } from 'svelte/store';


const createUserStore = (data) => {
    const userData = writable(data);

    const updateUserData = (newData) => {
        if (browser) localStorage.setItem("userData", JSON.stringify(newData))
        userData.update(() => newData);
    }

    const clearUserData = () => {
        if (browser) localStorage.removeItem("userData")
        userData.set(null);
    }

    const getUserData = () => get(userData)

    return { subscribe: userData.subscribe, update: updateUserData, clear: clearUserData, get: getUserData };
};

export const userData = createUserStore(browser && JSON.parse(localStorage.getItem("userData")));