select    t.name, count(*)
from      encounter e, encounter_type t
where     e.encounter_type = t.encounter_type_id
and       e.voided = 0
group by  t.name
order by  t.name