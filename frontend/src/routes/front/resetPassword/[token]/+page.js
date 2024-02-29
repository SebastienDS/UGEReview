async function validateToken(token) {
    try {
        const response = await fetch(`/api/v1/resetPassword/${token}/validate`);
        return response.ok
    } catch (error) {
        return false
    }
}

export async function load({ params }) {
    return {
        token: params.token,
        tokenValid: await validateToken(params.token),
    };
}