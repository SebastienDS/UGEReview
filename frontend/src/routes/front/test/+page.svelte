<nav>
  <a href="/front">Home</a>
  <a href="/front/test">Test</a>
  <a href="/front/reviews">Reviews</a>
  <a href="/front/login">Login</a>
  <a href="/front/logout">Logout</a>
</nav>


<h1>Test page</h1>

<script>
import { authToken } from '$lib/auth';

async function main() {
  try {
    console.log("token", $authToken)
    const response = await fetch("/api/v1/test", {
      headers: {
        'Authorization': $authToken
      }
    });
    console.log("response", response);
    console.log("body", await response.json())
  } catch (error) {
    console.error(error);
  }
}

main();

</script>