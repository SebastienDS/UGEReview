<script>
    export let notification;

    import { authToken } from '$lib/auth';
    import { createEventDispatcher } from 'svelte';


    const dispatch = createEventDispatcher()


    async function markAsRead() {
        try {
            const response = await fetch(`/api/v1/notifications/${notification.id}/markAsRead`, {
                method: 'POST',
                headers: {
                    'Authorization': authToken.get()
                }
            });
            if (!response.ok) return
            dispatch('markAsRead', { notificationId: notification.id })
        } catch (error) {
            console.log(error)
        }
    }

    async function markAsReadAndRedirect() {
        markAsRead(notification.id)
        dispatch('redirect', { link: notification.link })
    }
</script>

<div class="d-flex align-items-center">
    <form on:submit|preventDefault={markAsRead} class="me-3">
        <button type="submit" class="btn-close" aria-label="Close"></button>
    </form>
    <form on:submit|preventDefault={markAsReadAndRedirect} class="position-relative">
        <button type="submit" class="stretched-link btn btn-link">
            {JSON.stringify(notification)}
        </button>
    </form>
</div>
