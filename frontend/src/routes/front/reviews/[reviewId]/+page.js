import { authToken } from '$lib/auth';
import { goto } from '$app/navigation';


async function fetchData(reviewId) {
	try {
		const response = await fetch("/api/v1/reviews/" + reviewId);
		if (response.status !== 200) {
			goto("/front/error/" + response.status)
			return;
		}
		return response.json();
	} catch (error) {
		return { error: error }
	}
}

async function fetchNotificationState(reviewId) {
	try {
		const response = await fetch(`/api/v1/reviews/${reviewId}/notifications/state`, {
			headers: {
				'Authorization': authToken.get()
			}
		});
		var state = await response.json();
		return state.isUserRequestingNotification;
	} catch (error) {
		return false
	}
}

async function fetchNotificationStateIfAuthenticated(reviewId) {
	if (authToken.get() === null) return false;
	return fetchNotificationState(reviewId)
}

export async function load({ params }) {
	const [review, notificationActivated] = await Promise.all([
		fetchData(params.reviewId),
		fetchNotificationStateIfAuthenticated(params.reviewId)
	]);
    return {
        reviewId: params.reviewId,
        review,
        notificationActivated
    };
}