( async function() {
let data = {
  unitId: $unitId,
  containerId: $containerId,
  ASID: window.localStorage.getItem("ASID") === undefined ? -1 : window.localStorage.getItem("ASID"),
  MSISDN: window.localStorage.getItem("MSISDN") === undefined ? -1 : window.localStorage.getItem("MSISDN"),
  triggerId: $initId,
  event: "init",
};
const response = await fetch("http://192.168.1.105:8080/api/trigger", {
  method: "POST",
  mode: "cors",
  cache: "no-cache",
  headers: {
    "Content-Type": "application/json",
  },
  body: JSON.stringify(data),
});
const responseJson = await response.json();
window.localStorage.setItem("ASID", responseJson.asid);

setInterval(async () => {
    localStorage.setItem("ASID", 1);
    await fetch("http://192.168.1.105:8080/api/trigger", {
      method: "POST",
      mode: "cors",
      cache: "no-cache",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
         unitId: $unitId,
         containerId: $containerId,
         ASID: window.localStorage.getItem("ASID"),
         MSISDN: window.localStorage.getItem("MSISDN"),
         triggerId: $onlineId,
         event: "online",
       }),
    });
}, 10000)

let elements;
#foreach($trigger in $triggers)
elements = document.querySelectorAll("$trigger.getQuerySelector()");
for (let i = 0; i < elements.length; i++) {
  elements[i].addEventListener("$trigger.getEvent()", async () => {
    const response = await fetch("http://192.168.1.105:8080/api/trigger", {
      method: 'POST',
      mode: 'cors',
      cache: 'no-cache',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
       unitId: $trigger.getUnitId(),
       containerId: $trigger.getContainerId(),
       ASID: window.localStorage.getItem("ASID"),
       triggerId: $trigger.getTriggerId(),
       event: "$trigger.getEvent()",
     })
    });
  });
};

#end
})();