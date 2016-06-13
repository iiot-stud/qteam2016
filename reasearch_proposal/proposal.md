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

### 3.1 policy engine api design
The result of this work package is a REST API to communicate with our policy engine, containing all necessary information to enforce policies. Part of this work is describing how to possibly communicate with Ubernetes/Kubernetes.


#### Kubernetes ####
Kubernetes itself already contains a scheduler implementing a predicate and priority policy.

#### Ubernetes ####
<!-- x-cluster scheduling knowledge -->
A part of Ubernetes ist how to handle cross-cluster scheduling or migration by constraints like location affinity, available computing and network resources, availability, privacy, service level agreements, pricing or other policies. <!-- different vendor, capacity, network: bandwith, latency --> Additionally, to creating, moving, or replicating existing cluster instances, authorization and authentication has to be handled at some point.

<!-- demultiplexing -->Scheduling may demultiplex resource requests into multiples and assign to different clusters. Therefore some knowledge about internal coupling of components is required. Also, responses have to be remultiplexed into a single response.

<!-- Ubernetes API -->
Ubernetes Api is proposed to look like the existing Kubernetes API, with the extend of clusters being single objects to operate on, similar to nodes. This includes registering, listing, describing and deregistering clusters, but also requesting resources from a specific or, by some metric, automatically assigned cluster. This kind of automatic scheduling is proposed to be done by a not further defined, optional policy engine, which might be our proposed policy engine.

references
[Ubernetes Doc](https://github.com/kubernetes/kubernetes/blob/master/docs/proposals/federation.md)

## Impact / Outlook

## ((Ethical Issues))

## Resources to be committed
### ((Partner Description / Consortium as a whole))
### ((Payment Plan / Budget Split / ))

## References
