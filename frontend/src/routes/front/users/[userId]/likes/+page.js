async function fetchReviews(userId) {
    try {
        const likes = await fetch("/api/v1/users/" + userId + "/likes" );
        if (likes.status !== 200) {
            goto("/front/error/" + response.status)
            return;
        }
        return likes.json();
    } catch (error) {
        console.log(error);
        return { error: error }
    }
}

export async function load({ params }) {
    const [likes] = await Promise.all([
		fetchReviews(params.userId)
	]);
    return {
        likes,
        userId: params.userId
    };
}