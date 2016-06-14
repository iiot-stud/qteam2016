# Description of Work > Statement of Work > Technical Annex

## Title: A policy engine for container orchestration

## (Executive) Abstract

## Introduction
<!--- introducing issue -->
With increased interest in cloud computing and operating-system-level virtualization containers, the topic of container orchestration also gained interest. When trying to orchestrate across cloud provider borders, one of the different challenges is, to decide, which nodes from which provider to use, in a given situation.
<!--- accomplishments (own approach) -->
We want to elaborate a policy engine which decides, which cluster nodes meet the demands of a given policy by its given constraints. To do this, we need to identify criteria, on which we can define a set of policies on. We also need to define an interface to connect with orchestration software. When given a set of policies to enforce, potential contradictory policies have to be resolved in some manner. Once having an implementation, some form of auditing is desirable, to make sure policies are enforced.
<!--- research area -->
Our approach requires research in the practice of container orchestration and on the topic of semantic web. A better understanding of container orchestration allows us to understand the underlying technology we build upon and adapt accordingly. Semantic web helps us defining and evaluating policies. Because container orchestration should work on the internet, a prototype implementation of our policy engine requires understanding of web architecture and communication technologies. When policies should model law, for example to ensure privacy, an understanding of this topic has to be established accordingly.
<!-- gap -->
We want to apply semantic web methods to support container orchestration in enforcing policies, in particular using privacy laws as an example.
<!-- significance of research/Evaluation -->
Therefore our research improves knowledge on using semantic web technologies to enforce policies.

<!-- subheadings suggested by Alex
### Research Area
### Research Issue
### Related Work (Gap)
### Own Approach
### Evaluation
-->

## Related work (Gap)

### Übernetes
### ...

## Implementation

### Management Structure (WP-based + Scrum methodology + GANTT)
### WP1
#### T1.1
##### Lead: X, Participant: Y, Z
##### Effort: 3 Weeks
##### Description: Goal of this task is ... This includes aspect X and Y ...
##### Milestones & Deliverables

#### T1.x

### WPx

### 3.1 Policy Engine API Design
The result of this work package is a REST API to communicate with our policy engine, containing all necessary information to enforce policies. Part of this work is researching how to possibly communicate with Ubernetes/Kubernetes. With knowledge of Kubernetes/Ubernetes API we can thoroughly design our related API and also have the option to directly control Ubernetes/Kubernetes with our policy engine.

<!-- TODO
- Ubernetes/Kubernetes api
- (general) policy engine input/output
- already discussed input/output

possible implementation via Apiary.io or swagger.io

notes
- what do we get back from Ubernetes/Kubernetes if called
- we discussed to different use cases
    + customer requests cumputing nodes conforming (his given) policy
    + customer has a list of nodes and we apply a policy filter on it, returning only policy conform nodes
-->

#### Kubernetes ####
Kubernetes can be configured by its REST API, provided by the Kubernetes API server, or using its command-line interface, which itself calls the REST API.
The API itself has a versioned Uri and is centered around Kubernetes components: Node, Pod, Service and Replication Controller. These components can then use label, selector, namespace, annotation, name, secret (, volume...).
In return to a REST API call, mostly singular JSON objects are returned. These JSON Objects have a `kind` field, identifying the schema, and `apiVersion` field, for schema versioning.
<!-- TODO:
 further describe api conventions for a better understanding of the related components and their structure.

[Kubernetes API reference](https://github.com/kubernetes/kubernetes/blob/master/docs/api-reference/README.md)
[Kubernetes API conventions](https://github.com/kubernetes/kubernetes/blob/master/docs/devel/api-conventions.md)

example request: GET /api/v1/namespaces/{namespace}/endpoints

 starting points [http://kubernetes.io/docs/reference/], [http://kubernetes.io/docs/api/], [http://kubernetes.io/docs/user-guide/], [browsable api](http://kubernetes.io/kubernetes/third_party/swagger-ui/#/), [OpenShift Kubernetes api listing](https://docs.openshift.org/latest/rest_api/kubernetes_v1.html#paths)

-->

##### Kubernetes Scheduler #####
Kubernetes itself contains a pluggable scheduler to determine the placement of new pods onto cluster nodes. It has predicate and priority functions, on which a policy can be build on.
Predicates are requirements defined via labels on Kubernetes pods, which then get scheduled to nodes also having those labels and thus fulfilling the requirements.
Then the priority policy helps to rank this list of nodes, to decide which have precedence.

Below, the default policy configuration.
```
kind: "Policy"
version: "v1"
predicates:
  - name: "PodFitsPorts"
  - name: "PodFitsResources"
  - name: "NoDiskConflict"
  - name: "MatchNodeSelector"
  - name: "HostName"
priorities:
  - name: "LeastRequestedPriority"
    weight: 1
  - name: "BalancedResourceAllocation"
    weight: 1
  - name: "ServiceSpreadingPriority"
    weight: 1
```

`PodFitsPorts` checks for conflicting network ports on a node.
`PodFitsResources` determines if the resources defined in the pod requirements, are available on a node.
`NoDiskConflict` checks if the requested volume is not mounted by an other pod.
`MatchNodeSelector` tests if the pod's selector query is is satisfied by a node.
`HostName` checks if a node's name is equivalent to a possibly specified name in the pod's configuration.

`LeastRequestedPriority` prefers nodes with less load.
`BalancedResourceAllocation` favors nodes with similar memory and cpu load percentage in relation to maximum cpu and memory capacity. This should be used in conjunction with `LeastRequestedPriority`.
`ServiceSpreadingPriority` minimizes pods of the same service on the same node.


#### Ubernetes ####
<!-- x-cluster scheduling knowledge -->
A part of Ubernetes ist how to handle cross-cluster scheduling or migration by constraints like location affinity, available computing and network resources, availability, privacy, service level agreements, pricing or other policies. <!-- different vendor, capacity, network: bandwidth, latency --> Additionally, to creating, moving, or replicating existing cluster instances, authorization and authentication has to be handled at some point.

<!-- demultiplexing -->Scheduling may demultiplex resource requests into multiples and assign to different clusters. Therefore some knowledge about internal coupling of components is required. Also, responses have to be remultiplexed into a single response.

<!-- Ubernetes API -->
Ubernetes Api is proposed to look like the existing Kubernetes API, with the extend of clusters being single objects to operate on, similar to nodes. This includes registering, listing, describing and deregistering clusters, but also requesting resources from a specific or, by some metric, automatically assigned cluster. This kind of automatic scheduling is proposed to be done by a not further defined, optional policy engine, which might be our proposed policy engine.

references
<!-- Ubernetes -->
[Ubernetes Doc](https://github.com/kubernetes/kubernetes/blob/master/docs/proposals/federation.md)
<!-- Kubernetes Scheduler -->
[Kubernetes Scheduler Doc OpenShift](https://docs.openshift.org/latest/admin_guide/scheduler.html)
[Kubernetes policy types](https://github.com/kubernetes/kubernetes/blob/master/plugin/pkg/scheduler/api/types.go)
[Kubernetes Predicate Functions](https://github.com/kubernetes/kubernetes/blob/master/plugin/pkg/scheduler/algorithm/predicates/predicates.go)
[Kubernetes Priority Functions](https://github.com/kubernetes/kubernetes/blob/master/plugin/pkg/scheduler/algorithm/priorities/priorities.go)
<!-- Kubernetes API -->
[Kubernetes architecture](https://github.com/kubernetes/kubernetes/blob/master/docs/design/architecture.md)
[Kubernetes API reference](https://github.com/kubernetes/kubernetes/blob/master/docs/api-reference/README.md)
[Kubernetes API conventions](https://github.com/kubernetes/kubernetes/blob/master/docs/devel/api-conventions.md)

## Impact / Outlook

## ((Ethical Issues))

## Resources to be committed
### ((Partner Description / Consortium as a whole))
### ((Payment Plan / Budget Split / ))

## References
