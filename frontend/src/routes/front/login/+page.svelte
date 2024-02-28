<script>
import { authToken } from '$lib/auth';

async function login(username, password) {
  try {
    const response = await fetch("/api/v1/login", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username, password })
    });
    if (!response.ok) return

    const token = btoa(`${username}:${password}`)
    authToken.update(`Basic ${token}`);
    console.log("token", $authToken)
  } catch (error) {
    console.error(error);
  }
}

login("test", "test")
</script>

<nav>
  <a data-sveltekit-preload-data="tap" href="/front">Home</a>
  <a data-sveltekit-preload-data="tap" href="/front/test">Test</a>
  <a data-sveltekit-preload-data="tap" href="/front/reviews">Reviews</a>
  <a data-sveltekit-preload-data="tap" href="/front/login">Login</a>
  <a data-sveltekit-preload-data="tap" href="/front/logout">Logout</a>
</nav>


<h1>Login page</h1>