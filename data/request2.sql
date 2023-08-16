select cc.name, count(pp.id) from person pp join company cc ON pp.company_id = cc.id  group by cc.name having  count (pp.id) = (select max(c) from (select company_id, count(id) c from person group by company_id) AS t);



