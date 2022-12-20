const data = {
  unitId: $triggers[0].getUnitId(),
  containerId: $triggers[0].getContainerId(),
  ASID: window.localStorage.getItem("ASID"),
  MSISDN: 88005553535,
  event: "init",
};
const response = await fetch(192.168.1.105:8080/api/trigger, {
  method: "POST",
  mode: "cors",
  cache: "no-cache",
  headers: {
    "Content-Type": "application/json",
  },
  body: JSON.stringify({ data }),
});
const responceJson = await response.json();
window.localStorage.setItem("ASID", responceJson.ASID);

#foreach($trigger in $triggers)
let elements = querySelectorAll("$trigger.getQuerySelector()");
for (let i = 0; i < elements.length; i++) {
  const data = {
    unitId: $trigger.getUnitId(),
    containerId: $trigger.getContainerId(),
    ASID: window.localStorage.getItem("ASID"),
    triggerId: $trigger.getTriggerId(),
    event: $trigger.getEvent(),
  }
  elements[i].addEventListener($trigger.getEvent(), async () => {
    const response = await fetch(192.168.1.105:8080/api/trigger, {
      method: 'POST',
      mode: 'cors',
      cache: 'no-cache',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({data})
    });
  });
};

#end