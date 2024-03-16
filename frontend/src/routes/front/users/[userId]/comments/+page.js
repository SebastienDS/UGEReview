async function fetchReviews(userId) {
    try {
        const comments = await fetch("/api/v1/users/" + userId + "/comments" );
        if (comments.status !== 200) {
            goto("/front/error/" + response.status)
            return;
        }
        return comments.json();
    } catch (error) {
        console.log(error);
        return { error: error }
    }
}

export async function load({ params }) {
    const [comments] = await Promise.all([
		fetchReviews(params.userId)
	]);
    return {
        comments,
        userId:params.userId
    };
}