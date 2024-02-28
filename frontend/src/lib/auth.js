import { browser } from "$app/environment";
import { get, writable } from 'svelte/store';


const createAuthTokenStore = (token) => {
    const authToken = writable(token);

    const updateToken = (newToken) => {
        if (browser) localStorage.setItem("basicAuthToken", newToken)
        authToken.update(() => newToken);
    }

    const clearToken = () => {
        if (browser) localStorage.removeItem("basicAuthToken")
        authToken.set(null);
    }

    const getToken = () => get(authToken)

    return { subscribe: authToken.subscribe, update: updateToken, clear: clearToken, get: getToken };
};

export const authToken = createAuthTokenStore(browser && localStorage.getItem("basicAuthToken"));