import { authToken } from '$lib/auth';
import { userData } from '$lib/userData';

async function fetchReviews(userId) {
    try {
        const follows = await fetch("/api/v1/users/" + userId + "/follows" );
        if (follows.status !== 200) {
            goto("/front/error/" + response.status)
            return;
        }
        return follows.json();
    } catch (error) {
        console.log(error);
        return { error: error }
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
    const [follows] = await Promise.all([
		fetchReviews(params.userId)
	]);
    return {
        follows,
        userId: params.userId,
        isMyUserPage: isMyUserPage(params.userId),
        isAuthenticated: authToken.get() != null,
    };
}