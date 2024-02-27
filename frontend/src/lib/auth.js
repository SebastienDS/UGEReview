import { browser } from "$app/environment";
import { writable } from 'svelte/store';


const createAuthTokenStore = (token) => {
    const { subscribe, set, update } = writable(token);

    const updateToken = (newToken) => {
        if (browser) localStorage.setItem("basicAuthToken", newToken)
        update(() => newToken);
    }

    const clearToken = () => {
        if (browser) localStorage.removeItem("basicAuthToken")
        set(null);
    }

    return { subscribe, update: updateToken, clear: clearToken };
};

export const authToken = createAuthTokenStore(browser && localStorage.getItem("basicAuthToken"));