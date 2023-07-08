import http from 'k6/http';

export const options = {
  scenarios: {
    my_scenario1: {
      executor: 'constant-arrival-rate',
      duration: '30s', // total duration
      preAllocatedVUs: 4, // to allocate runtime resources     preAll

      rate: 5, // number of constant iterations given `timeUnit`
      timeUnit: '1s',
    },
  },
};

export default function () {

  http.get('http://localhost:8080/quota/test-1')
}
