import Swal from "sweetalert2";
import "sweetalert2/src/sweetalert2.scss";

export default {
    success: (title: string|null|undefined, message: string) => {
        Swal.fire({
            title: title || "Success!",
            text: message,
            icon: "success"
        });
    },
    error: (title: string|null|undefined, message: string) => {
        Swal.fire({
            title: title || "Error!",
            text: message,
            icon: "error"
        });
    }
}