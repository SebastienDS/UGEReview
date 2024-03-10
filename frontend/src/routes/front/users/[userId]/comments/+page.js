async function fetchReviews(userId) {
    try {
        const comments = await fetch("/api/v1/users/" + userId + "/comments" );
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
        comments
    };
}