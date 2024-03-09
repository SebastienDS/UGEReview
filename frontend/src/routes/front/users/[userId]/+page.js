import { authToken } from '$lib/auth';
import { userData } from '$lib/userData';

async function fetchData(userId) {
	try {
		const response = await fetch("/api/v1/users/" + userId, {
			headers: {
				'Authorization': authToken.get()
			}
		});
		return response.json();
	} catch (error) {
		return { error: error }
	}
}

async function fetchFollowState(userId) {
    try {
		const response = await fetch(`/api/v1/users/${userId}/follow/state`, {
			headers: {
				'Authorization': authToken.get()
			}
		});
		var state = await response.json();
		return state.isUserFollowing;
	} catch (error) {
		return false
	}
}

async function fetchFollowsIfConnected(userId) {
    if (authToken.get() != null && userData.get().id != userId) {
        return fetchFollowState(userId);
    }
	if (authToken.get() != null && userData.get().id == userId) {
		return false;
	} 
}

function isMyUserPage(userId) {
	const user = userData.get();
	if (user == null) {
		return false;
	}
	return user.id == userId;
}

export async function load({ params }) {
	const [user, checkFollow] = await Promise.all([
		fetchData(params.userId),
		fetchFollowsIfConnected(params.userId)
	]);
    return { 
        user,
        isMyUserPage: isMyUserPage(params.userId),
        isAuthenticated: authToken.get() != null,
		checkFollow
    };
}