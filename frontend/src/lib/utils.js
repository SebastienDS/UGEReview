export function formatDate(date) {
    function pad(number) {
        if (number < 10) {
            return "0" + number;
        }
        return number;
    }

    let day = pad(date.getDate());
    let month = pad(date.getMonth() + 1);
    let year = date.getFullYear();
    let hours = pad(date.getHours());
    let minutes = pad(date.getMinutes());
    let seconds = pad(date.getSeconds());

    return `${day}/${month}/${year} ${hours}:${minutes}:${seconds}`;
}