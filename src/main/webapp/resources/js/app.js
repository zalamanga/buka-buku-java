"use strict";
document.addEventListener("submit", function (event) {
  const form = event.target;
  const status = form.querySelector(".submit-status");
  if (status) {
    status.textContent = "Memproses permintaan…";
    form.setAttribute("aria-busy", "true");
  }
});
