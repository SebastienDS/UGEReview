import { browser } from "$app/environment";
import { goto } from '$app/navigation';
import { authToken } from '$lib/auth';


export const requireAuth = () => {
  if (browser && !authToken.get()) {
    goto('/front/login');
    return false;
  }
  return true;
}