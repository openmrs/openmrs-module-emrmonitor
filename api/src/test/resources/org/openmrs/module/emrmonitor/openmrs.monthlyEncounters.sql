select    t.name, FORMATDATETIME(e.encounter_datetime,'yyyyMMdd'), count(*) as num
from      encounter e, encounter_type t
where     e.encounter_type = t.encounter_type_id
and       e.voided = 0
group by  t.name, FORMATDATETIME(e.encounter_datetime,'yyyyMMdd')
order by  t.name, FORMATDATETIME(e.encounter_datetime,'yyyyMMdd')