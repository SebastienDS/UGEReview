<script>
import { goto } from '$app/navigation';
import { authToken } from '$lib/auth';
import { userData } from '$lib/userData';


async function logout() {
  try {
    const response = await fetch("/logout", {
      headers: {
        'Authorization': $authToken
      }
    });
    if (!response.ok) return

    authToken.clear();
    userData.clear();

    goto("/front")
  } catch (error) {
    console.error(error);
  }
}

logout()
</script>