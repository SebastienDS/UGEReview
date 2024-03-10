import { browser } from "$app/environment";
import { get, writable } from 'svelte/store';


const createAuthTokenStore = (token) => {
    const authToken = writable(token);

    const updateToken = (username, password) => {
        const newToken = `Basic ${btoa(`${username}:${password}`)}`
        if (browser) localStorage.setItem("basicAuthToken", newToken)
        authToken.update(() => newToken);
    }

    const getCredentials = (currentToken) => {
        const base64Credentials = currentToken.split(' ')[1];
        const credentials = atob(base64Credentials);
        return credentials.split(':');
    }

    const updateUsername = (username) => {
        const [_, password] = getCredentials(get(authToken))
        updateToken(username, password)
    }

    const updatePassword = (password) => {
        const [username, _] = getCredentials(get(authToken))
        updateToken(username, password)
    }

    const clearToken = () => {
        if (browser) localStorage.removeItem("basicAuthToken")
        authToken.set(null);
    }

    const getToken = () => get(authToken)

    return { subscribe: authToken.subscribe, update: updateToken, clear: clearToken, get: getToken, updateUsername, updatePassword };
};

export const authToken = createAuthTokenStore(browser && localStorage.getItem("basicAuthToken"));