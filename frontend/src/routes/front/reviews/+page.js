import { authToken } from '$lib/auth';


async function fetchReviews() {
    try {
        const response = await fetch("/api/v1/reviews", {
            headers: {
                'Authorization': authToken.get()
            }
        });
        return response.json();
    } catch (error) {
        console.log(error);
        return { error: error }
    }
}

async function fetchNotifications() {
    try {
        const response = await fetch("/api/v1/notifications", {
            headers: {
                'Authorization': authToken.get()
            }
        });
        return response.json();
    } catch (error) {
        console.log(error);
        return { error: error }
    }
}

async function fetchNotificationsIfAuthenticated() {
	if (authToken.get() === null) return false;
	return fetchNotifications()
}

export async function load({ params }) {
    const [reviews, notifications] = await Promise.all([
		fetchReviews(),
		fetchNotificationsIfAuthenticated()
	]);
    return {
        reviews,
        notifications
    };
}