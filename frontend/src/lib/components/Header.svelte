<script>
    import { authToken } from '$lib/auth';
    import Notification from './Notification.svelte';

    const isAuthenticated = authToken.get() != null;
    let notifications = [];

    async function fetchNotifications() {
        try {
            const response = await fetch("/api/v1/notifications", {
                headers: {
                    'Authorization': authToken.get()
                }
            });
            notifications = await response.json();
        } catch (error) {
            console.log(error);
        }
    }

    async function fetchNotificationsIfAuthenticated() {
        if (authToken.get() === null) return;
        fetchNotifications()
    }

    async function markNotificationAsRead(e) {
        const notificationId = e.detail.notificationId
        notifications = notifications.filter(n => n.id != notificationId)
    }

    async function redirectToNotification(e) {
        goto(e.detail.link)
    }

    fetchNotificationsIfAuthenticated()

</script>

<nav class="navbar navbar-expand-lg">
  <div class="container-fluid" style="background-color: lightblue;">
    <a class="navbar-brand" href="/front/reviews">
      <img width="60px" height="60px" src="/images/logo.png" alt="logo">
    </a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
            aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav">
        <li class="nav-item">
          <a class="nav-link" href="/front/reviews">Accueil</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="/front/createReview">Créer une revue</a>
        </li>
      </ul>
    </div>

    {#if isAuthenticated}
        <div class="dropdown mx-3">
            <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownNotifications" data-bs-toggle="dropdown" data-bs-auto-close="outside"  aria-expanded="false">
                Notifications {#if notifications.length > 0}
                    <span>({notifications.length})</span>
                {/if}
            </button>

            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="dropdownNotifications" style="min-height: 100px;">
                {#each notifications as notification}
                    <li class="dropdown-item d-flex align-items-center">
                        <Notification notification={notification} on:markAsRead={markNotificationAsRead} on:redirect={redirectToNotification}/>
                    </li>   
                {/each}
            </ul>
        </div>
    {/if}

    <a class="navbar-brand" href="/front/profile">
        <img width="60px" height="60px" src="/images/user.png" alt="profile">
    </a>
  </div>

</nav>


